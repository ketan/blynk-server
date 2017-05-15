import React from 'react';
import './styles.less';
import Dotdotdot from 'react-dotdotdot';
import classnames from 'classnames';

class FieldStub extends React.Component {

  static propTypes = {
    multipleLines: React.PropTypes.bool,
    inline: React.PropTypes.bool,
    children: React.PropTypes.any
  };

  render() {

    const className = classnames({
      'product-metadata-static-field': !this.props.inline,
      'no-value': !this.props.children,
      'product-metadata-static-field-inline': this.props.inline
    });

    return (
      <div className={className}
           style={{wordWrap: 'break-word'}}>
        { !this.props.multipleLines && (
          <Dotdotdot clamp={1}>
            { this.props.children || 'No Value' }
          </Dotdotdot>
        ) || (
          <p>{ this.props.children || 'No Value' }</p>
        )}
      </div>
    );
  }

}

export default FieldStub;
