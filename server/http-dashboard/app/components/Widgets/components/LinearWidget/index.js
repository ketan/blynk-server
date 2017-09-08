import React from 'react';
import {
  Plotly
} from 'components';
import moment from 'moment';
// import Widget from '../Widget';
import {VIRTUAL_PIN_PREFIX} from 'services/Widgets';
import PropTypes from 'prop-types';
import LinearWidgetSettings from './settings';
import './styles.less';
import {connect} from 'react-redux';
import {Map} from 'immutable';
import _ from 'lodash';

@connect((state) => ({
  widgets: state.Widgets && state.Widgets.get('widgetsData'),
}))
class LinearWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,
    params: PropTypes.object,

    editable: PropTypes.bool,

    fetchRealData: PropTypes.bool,

    onWidgetDelete: PropTypes.func,

    widgets: PropTypes.instanceOf(Map),
  };

  state = {
    data: []
  };

  componentWillMount() {

    if(!this.props.fetchRealData) {
      this.generateFakeData();
    }

  }

  componentDidUpdate(prevProps) {
    if(!this.props.fetchRealData && this.state.data.length !== this.props.data.sources.length) {
      this.generateFakeData();
    }

    if(!_.isEqual(prevProps.data.sources, this.props.data.sources)) {
      this.generateFakeData();
    }
  }

  generateFakeData() {
    const data = [];

    this.props.data.sources.forEach((source) => {

      const y = [];

      for(let i = 0; i < 5; i++) {
        y.push(_.random(0, 10));
      }

      data.push({
        name: source.label,
        x: [1,2,3,4,5],
        y: y,
        mode: 'lines+markers',
        marker: {
          color: source.color
        }
      });

    });

    this.setState({
      data: data
    });
  }

  layout = {
    autosize: true,
    margin: {
      t: 30,
      r: 30,
      l: 30,
      b: 60,
    },
    xaxis: {
      ticklen: 0,
      tickangle: 40,
      nticks: 12,
    }
  };

  config = {
    displayModeBar: false
  };

  renderRealDataChart() {
    if (!this.props.data.sources || !this.props.data.sources.length)
      return null;

    const data = [];

    if (!this.props.widgets.hasIn([this.props.params.id, 'loading']) || this.props.widgets.getIn([this.props.params.id, 'loading']))
      return null;

    this.props.data.sources.forEach((source) => {

      if (!source.dataStream || source.dataStream.pin === -1)
        return null;

      const PIN = this.props.widgets.getIn([this.props.params.id, `${VIRTUAL_PIN_PREFIX}${source.dataStream.pin}`]);

      if(!PIN)
        return null;

      let x = [];
      let y = [];

      PIN.get('data').forEach((item) => {
        x.push(moment(Number(item.get('x'))).format('YYYY-MM-DD HH:mm:ss'));
        y.push(item.get('y'));
      });

      data.push({
        name: source.label,
        x: x,
        y: y,
        mode: 'lines+markers',
        marker: {
          color: source.color
        }
      });

    });

    if(this.props.data.width <= 8 && this.props.data.width >= 6)
      this.layout.xaxis.nticks = 15;

    if(this.props.data.width === 5)
      this.layout.xaxis.nticks = 12;

    if(this.props.data.width <= 4 && this.props.data.width > 3)
      this.layout.xaxis.nticks = 8;

    if(this.props.data.width === 3)
      this.layout.xaxis.nticks = 6;

    return (
      <div className="grid-linear-widget">
        <Plotly data={data} config={this.config} layout={this.layout}/>
      </div>
    );
  }

  renderFakeDataChart() {

    if(this.props.data.width <= 8 && this.props.data.width >= 6)
      this.layout.xaxis.nticks = 15;

    if(this.props.data.width === 5)
      this.layout.xaxis.nticks = 12;

    if(this.props.data.width <= 4 && this.props.data.width > 3)
      this.layout.xaxis.nticks = 8;

    if(this.props.data.width === 3)
      this.layout.xaxis.nticks = 6;

    return (
      <div className="grid-linear-widget">
        <Plotly data={this.state.data} config={this.config} layout={this.layout}/>
      </div>
    );
  }

  render() {

    if (!this.props.fetchRealData)
      return this.renderFakeDataChart();

    return this.renderRealDataChart();
  }

}

LinearWidget.Settings = LinearWidgetSettings;

export default LinearWidget;


/*
* 1) Get data for own PINS
* 2) Draw data for these own PINS
* 3) Display labels for these PINS
* 4) Fix DataStreams
* 5) Multiple sources support */
