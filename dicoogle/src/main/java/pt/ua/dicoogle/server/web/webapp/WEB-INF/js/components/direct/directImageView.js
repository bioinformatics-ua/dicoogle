var React = require('react');
var ReactBootstrap = require('react-bootstrap');
var Button = ReactBootstrap.Button;
var ModalTrigger = ReactBootstrap.ModalTrigger;
var Modal = ReactBootstrap.Modal;

import {getUrlVars} from '../../utils/url';

import {Endpoints} from '../../constants/endpoints';

var DirectImageView = React.createClass({
	getInitialState: function() {
    	return {data: [],
    	status: "loading",
    	current: 0};
  	},


	render:function(){
	  var url = Endpoints.base + "/dic2png?SOPInstanceUID="+getUrlVars()['SOPInstanceUID'];
		return (

             <img  id="image1" src={url} width="100%" height="100%" onError={this.imageLoadError} />

			);
	}
  ,
  imageLoadError:function(){
    var img = document.getElementById('image1');
    img.src="assets/image-not-found.png";
    img.style.width="auto";
    img.style.height="300px";

  }
});



export {DirectImageView};
