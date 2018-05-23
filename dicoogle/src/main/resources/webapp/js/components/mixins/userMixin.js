import { Router } from "react-router";
import UserStore from "../../stores/userStore";

const UserMixin = {
  mixins: [Router.Navigation],
  componentWillMount: function() {
    if (UserStore.getLogginState() === false) {
      console.log("usermixin", "NOOOO");
      this.transitionTo("loading");
    } else {
      console.log("usermixin", "yesss");
      document.getElementById("container").style.display = "block";
    }
  }
};

export { UserMixin };
