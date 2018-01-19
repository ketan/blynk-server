import React from 'react';
import {
  AddWidgetTools,
  Grid,
  DeviceSelect,
} from './components';
import PropTypes from 'prop-types';
import {getNextId} from 'services/Products';
import {fromJS} from 'immutable';
import {getCoordinatesToSet} from 'services/Widgets';
import './styles.less';
import _ from 'lodash';


class Dashboard extends React.Component {

  static propTypes = {
    fields: PropTypes.object,
  };

  constructor(props) {
    super(props);

    this.handleWidgetAdd = this.handleWidgetAdd.bind(this);
    this.handleWidgetDelete = this.handleWidgetDelete.bind(this);
    this.handleWidgetClone = this.handleWidgetClone.bind(this);
  }

  handleWidgetDelete(id) {
    let fieldIndex = null;

    this.props.fields.getAll().forEach((field, index) => {
      if(Number(field.id) === Number(id))
        fieldIndex = index;
    });

    this.props.fields.remove(fieldIndex);
  }

  handleWidgetClone(id, breakPoint) {

    const widgets = this.props.fields.getAll();

    const widget = _.find(widgets, (widget) => Number(widget.id) === id);

    const coordinatesForNewWidget = getCoordinatesToSet(widget, widgets, breakPoint);

    this.props.fields.push({
      ...widget,
      id: getNextId(this.props.fields.getAll()),
      label: `${widget.label} Copy`,
      x: coordinatesForNewWidget.x,
      y: coordinatesForNewWidget.y,
    });

  }

  handleWidgetAdd(widget) {

    const widgets = this.props.fields.getAll();

    const coordinatesForNewWidget = getCoordinatesToSet(widget, widgets, 'lg'); //hardcoded breakPoint as we have only lg for now

    this.props.fields.push({
      ...widget,
      id: getNextId(this.props.fields.getAll()),
      x: coordinatesForNewWidget.x,
      y: coordinatesForNewWidget.y,
    });
  }

  render() {

    const widgets = fromJS(this.props.fields.map((prefix, index, fields) => {
      return {
        ...fields.get(index),
        fieldName: prefix,
      };
    }));

    const params = {
      id: 1
    };

    const deviceId = 1;

    const devicesList = [
      {
        key: '1',
        value: 'Test Device 1'
      },
      {
        key: '2',
        value: 'Test Device 2'
      },
      {
        key: '3',
        value: 'Test Device 3'
      }
    ];

    const onChange = () => {

    };

    return (
      <div className="products-manage-dashboard">

        <div className={`products-manage-dashboard--tools`}>
          <div className={`products-manage-dashboard--tools--widget-add`}>
            <AddWidgetTools onWidgetAdd={this.handleWidgetAdd}/>
          </div>
          <div className={`products-manage-dashboard--tools--device-select`}>
            <DeviceSelect devicesList={devicesList} onChange={onChange}/>
          </div>
        </div>

        <Grid widgets={widgets}
              params={params}
              deviceId={Number(deviceId)}
              onWidgetDelete={this.handleWidgetDelete}
              onWidgetClone={this.handleWidgetClone}
        />

      </div>
    );
  }

}

export default Dashboard;
