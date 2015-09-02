var React = require('react');
var ReactBootstrap = require('react-bootstrap');

import {unindex} from '../../../handlers/requestHandler';
import {ConfirmModal} from './confirmModal';

var AdvancedOptionsView = React.createClass({
	componentDidMount: function(){
		
	},
	componentDidUpdate: function(){
	
	},
	render: function() {
		return ( 
			<button class="btn btn-default" type="submit" onclick="" onClick={self.onUnindexClick.bind(null)}>Button</button>
		);
	},
	onUnindexClick:function(id, index){
		var uris = []; 
		for(let s in this.props.items.)
			for(let ss in this.props.items.results[index].studies[s].series)
				for(let i in this.props.items.results[index].studies[s].series[ss].images)
					uris.push(this.props.items.results[index].studies[s].series[ss].images[i].uri);
		
		let p = this.props.provider;

		unindex(uris, p, 
				function() {
			console.log("sucess");
		}, function(){
			console.log("Error");
		});
	}
});

export {AdvancedOptionsView};
