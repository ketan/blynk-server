import {API_URL} from "services/API/index";

export const DevicesFetch = (data) => {

  if (!data.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICES_FETCH',
    payload: {
      request: {
        method: 'get',
        url: `/devices/${data.orgId}`
      }
    }
  };
};

export const DeviceFetch = (params, data) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_FETCH',

    payload: {
      request: {
        method: 'get',
        url: `/devices/${params.orgId}/${data.id}`
      }
    }
  };
};

export const DeviceUpdate = (data, device) => {

  if (!data.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_UPDATE',
    payload: {
      request: {
        method: 'post',
        url: `/devices/${data.orgId}`,
        data: device
      }
    }
  };
};

export const DeviceCreate = (data, device) => {

  if (!data.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_CREATE',
    payload: {
      request: {
        method: 'put',
        url: `/devices/${data.orgId}`,
        data: device
      }
    }
  };
};

export const DeviceDelete = (deviceId, orgId) => {

  if (!orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_DELETE',
    payload: {
      request: {
        method: 'delete',
        url: `/devices/${orgId}/${deviceId}`
      }
    }
  };
};

export const TimelineFetch = (params = {}) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  if (!params.deviceId) {
    throw new Error('Required parameter deviceId is missed');
  }

  // default parameters

  params = Object.assign({}, {
    from: 0,
    to: new Date().getTime(),
    limit: 50,
    offset: 0
  }, params);

  return {
    type: 'API_TIMELINE_FETCH',
    payload: {
      request: {
        method: 'get',
        url: `/devices/${params.orgId}/${params.deviceId}/timeline`,
        params: params
      }
    }
  };
};

export const TimelineResolve = (params) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  if (!params.eventId || !params.deviceId)
    throw new Error('Required parameter is missed');

  return {
    type: 'API_TIMELINE_RESOLVE',
    payload: {
      request: {
        method: 'post',
        url: `/devices/${params.orgId}/${params.deviceId}/resolveEvent/${params.eventId}`,
        data: {
          comment: params.comment
        }
      }
    }
  };
};

export const DeviceDetailsFetch = (params, data) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_DETAILS_FETCH',

    payload: {
      request: {
        method: 'get',
        url: `/devices/${params.orgId}/${data.id}`
      }
    }
  };
};

export const DeviceDetailsUpdate = (params, data) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_DETAILS_UPDATE',
    payload: {
      request: {
        method: 'post',
        url: API_URL.device().update(params),
        data: data
      }
    }
  };
};

export const DeviceDashboardFetch = ({orgId}) => {

  if(!orgId)
    throw new Error('Parameter orgId is missed');

  return {
    type: 'API_DEVICE_DASHBOARD_FETCH',

    payload: {
      request: {
        method: 'get',
        url: API_URL.device().get({ orgId })
      }
    }
  };
};

export const DeviceAvailableOrganizationsFetch = () => {

  return {
    type: 'API_DEVICE_AVAILABLE_ORGANIZATIONS_FETCH',

    payload: {
      request: {
        method: 'get',
        url: API_URL.organization()
      }
    }
  };
};

export const DeviceMetadataUpdate = (params, metadata) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  if (!params.deviceId)
    throw new Error('deviceId parameter is missed');

  return {
    type: 'API_DEVICE_METADATA_UPDATE',

    payload: {
      request: {
        method: 'post',
        url: API_URL.device().metadata().update(params),
        data: metadata
      }
    }
  };
};
