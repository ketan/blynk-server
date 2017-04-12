import React from 'react';

import {Select} from 'antd';

import {UsersAvailableRoles, Roles} from 'services/Roles';

import './styles.less';

export default class Role extends React.Component {

  static propTypes = {
    role: React.PropTypes.string,
    onChange: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.state = {
      role: props.role
    };
  }

  getRolesList() {
    const options = [];
    UsersAvailableRoles.forEach((role) => {
      options.push(<Select.Option key={role.value} disabled={role.disabled}>{role.title}</Select.Option>);
    });
    return options;
  }

  onChange(value) {
    this.setState({role: value.key});
    if (this.props.onChange) this.props.onChange(value);
  }

  render() {

    const options = this.getRolesList();

    return (
      (this.state.role === Roles.SUPER_ADMIN.value && <div>{Roles.SUPER_ADMIN.title}</div> ) || (
        <Select labelInValue className="user--role-select"
                value={{key: this.state.role}}
                onChange={this.onChange.bind(this)} disabled={this.state.role === Roles.SUPER_ADMIN.value}>
        { options }
        </Select>)
    );
  }

}
