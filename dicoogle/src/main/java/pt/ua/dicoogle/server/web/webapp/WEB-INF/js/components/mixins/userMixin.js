var Router = require('react-router');
import {UserStore} from '../../stores/userStore';

var UserMixin = {
  mixins: [Router.Navigation],
  componentWillMount: function() {
    if(UserStore.getLogginState() === false)
    {
      console.log("usermixin", "NOOOO");
      this.transitionTo('loading');
    }
    else{
      console.log("usermixin", "yesss");
      document.getElementById('container').style.display = 'block';
    }
  }

};

export {UserMixin}
