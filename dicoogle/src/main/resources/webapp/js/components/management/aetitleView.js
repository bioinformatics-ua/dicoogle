import React from "react";
import AETitleForm from "./aetitleForm";
import * as AETitleActions from "../../actions/aetitleActions";
import AETitleStore from "../../stores/aetitleStore";
import Webcore from "dicoogle-webcore";

const AETitleView = React.createClass({
    getInitialState() {
        return {
            aetitleText: "",
            dirtyValue: false,  // aetitle value has unsaved changes
            status: "done"
        };
    },

    componentWillMount() {
        this.unsubscribe = AETitleStore.listen(this._onChange);
    },

    componentWillUnmount() {
        this.unsubscribe();
    },

    componentDidMount() {
        AETitleActions.getAETitle();
    },

    _onChange(data) {
        console.log(data);
        this.setState({
            aetitleText: data.aetitleText,
            status: "done"
        });
    },

    render() {
        if (this.state.status === "loading") {
            return (
                <div className="loader-inner ball-pulse">
                    <div />
                    <div />
                    <div />
                </div>
            );
        }

        return (
            <div className="panel panel-primary topMargin">
                <div className="panel-heading">
                    <h3 className="panel-title">AETitle</h3>
                </div>
                <div className="panel-body">
                    <AETitleForm
                        aetitleText=""
                        onChangeAETitle={this.handleAETitleChange}
                        onSubmitAETitle={this.handleSubmitAETitle}
                        dirtyValue={this.state.dirtyValue}
                    />
                </div>
            </div>
        );
    },

    handleAETitleChange(aetitle) {
        console.log("handleAETitleChange");
        this.setState({
            aetitleText: aetitle,
            dirtyValue: true
        });
    },

    handleSubmitAETitle(aetitle) {
        console.log("handleSubmitAETitle", aetitle);
        this.setState({
            dirtyValue: false
        });

        AETitleActions.setAETitle(aetitle);
    }
});

export { AETitleView };
