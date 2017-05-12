import React from 'react';
import Event from './../Event';
import {EVENT_TYPES} from 'services/Products';

class Critical extends React.Component {

  static propTypes = {
    form: React.PropTypes.string,
    initialValues: React.PropTypes.object,
    onChange: React.PropTypes.func,
    onClone: React.PropTypes.func,
    onDelete: React.PropTypes.func,
    validate: React.PropTypes.func
  };

  render() {
    return (
      <Event type={EVENT_TYPES.CRITICAL} form={this.props.form} initialValues={this.props.initialValues}
             onChange={this.props.onChange}
             onClone={this.props.onClone}
             validate={this.props.validate}
             onDelete={this.props.onDelete}/>
    );
  }

}

export default Critical;
