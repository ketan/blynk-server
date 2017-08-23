package cc.blynk.core.http.rest;

import cc.blynk.core.http.UriTemplate;
import cc.blynk.core.http.annotation.*;
import cc.blynk.core.http.rest.params.Param;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.exceptions.WebException;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.stats.GlobalStats;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static cc.blynk.core.http.Response.*;
import static cc.blynk.server.core.protocol.enums.Command.HTTP_TOTAL;

/**
 * Wrapper around Singleton Services.
 * Holds all info about annotations and service purpose.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.12.15.
 */
public class HandlerWrapper {

    private static final Logger log = LogManager.getLogger(HandlerWrapper.class);

    public final UriTemplate uriTemplate;

    public final HttpMethod httpMethod;

    public final Method classMethod;

    public final Object handler;

    public final Param[] params;

    public final Role allowedRoleAccess;

    public final short metricIndex;

    public final GlobalStats globalStats;

    public HandlerWrapper(UriTemplate uriTemplate, Method method, Object handler, GlobalStats globalStats) {
        this.uriTemplate = uriTemplate;
        this.classMethod = method;
        this.handler = handler;

        if (method.isAnnotationPresent(POST.class)) {
            this.httpMethod = HttpMethod.POST;
        } else if (method.isAnnotationPresent(PUT.class)) {
            this.httpMethod = HttpMethod.PUT;
        } else if (method.isAnnotationPresent(DELETE.class)) {
            this.httpMethod = HttpMethod.DELETE;
        } else {
            this.httpMethod = HttpMethod.GET;
        }

        Metric metricAnnotation = method.getAnnotation(Metric.class);
        if (metricAnnotation != null) {
            metricIndex = metricAnnotation.value();
        } else {
            metricIndex = -1;
        }
        if (method.isAnnotationPresent(SuperAdmin.class)) {
            this.allowedRoleAccess = Role.SUPER_ADMIN;
        } else if (method.isAnnotationPresent(Admin.class)) {
            this.allowedRoleAccess = Role.ADMIN;
        } else if (method.isAnnotationPresent(Staff.class)) {
            this.allowedRoleAccess = Role.STAFF;
        } else {
            this.allowedRoleAccess = null;
        }

        this.params = new Param[method.getParameterCount()];
        this.globalStats = globalStats;
    }

    public Object[] fetchParams(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        Object[] res = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            res[i] = params[i].get(ctx, uriDecoder);
        }

        return res;
    }

    public FullHttpResponse invoke(Object[] params) {
        try {
            mark();
            return (FullHttpResponse) classMethod.invoke(handler, params);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof ForbiddenWebException) {
                return forbidden(cause.getMessage());
            }
            if (cause instanceof WebException) {
                return badRequest(cause.getMessage());
            }

            log.error("Error invoking handler. Reason : {}.", e.getMessage());

            if (e instanceof InvocationTargetException) {
                Throwable originalException = ((InvocationTargetException) e).getTargetException();
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement element : originalException.getStackTrace()) {
                   sb.append(element)
                     .append("\n");
                }
                log.error(sb.toString());
            }

            return serverError("Error invoking handler.");
        }
    }

    private void mark() {
        globalStats.mark(HTTP_TOTAL);
        if (metricIndex > -1) {
            globalStats.markSpecificCounterOnly(metricIndex);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HandlerWrapper)) return false;

        HandlerWrapper that = (HandlerWrapper) o;

        if (uriTemplate != null ? !uriTemplate.equals(that.uriTemplate) : that.uriTemplate != null) return false;
        return !(httpMethod != null ? !httpMethod.equals(that.httpMethod) : that.httpMethod != null);

    }

    @Override
    public int hashCode() {
        int result = uriTemplate != null ? uriTemplate.hashCode() : 0;
        result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
        return result;
    }
}
