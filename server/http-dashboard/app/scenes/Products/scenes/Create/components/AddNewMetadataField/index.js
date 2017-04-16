import React from 'react';
import {Button, /*Menu, Dropdown, Icon*/} from 'antd';
import './styles.less';

// const menu = (
//   <Menu>
//     <Menu.Item>
//       Text
//     </Menu.Item>
//     <Menu.Item>
//      Number
//     </Menu.Item>
//     <Menu.Item>
//      Unit
//     </Menu.Item>
//     <Menu.Item>
//       Cost
//     </Menu.Item>
//     <Menu.Item>
//       Switch
//     </Menu.Item>
//     <Menu.Item>
//       List
//     </Menu.Item>
//   </Menu>
// );

class AddNewMetadataField extends React.Component {
  render() {
    return (
      <div className="products-add-new-metadata-field">
        <div className="products-add-new-metadata-field-title">+ Add new Metadata Field:</div>
        <div className="products-add-new-metadata-field-fields">
          <Button type="dashed">Text</Button>
          <Button type="dashed">Number</Button>
          <Button type="dashed">Unit</Button>
          <Button type="dashed">Cost</Button>
          <Button type="dashed">Contact</Button>
        </div>
        {/* @todo uncomment when add additional types of metadata*/}
        {/*<div className="products-add-new-metadata-field-other-fields">*/}
        {/*<Dropdown overlay={menu}>*/}
        {/*<a className="ant-dropdown-link" href="#">*/}
        {/*Other Metadata Types <Icon type="down" />*/}
        {/*</a>*/}
        {/*</Dropdown>*/}
        {/*</div>*/}
      </div>
    );
  }
}

export default AddNewMetadataField;
