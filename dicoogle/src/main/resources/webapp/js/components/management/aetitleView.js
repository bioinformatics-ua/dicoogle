import React from "react";
import AETitleForm from "./aetitleForm";
import * as AETitleActions from "../../actions/aetitleActions";
import AETitleStore from "../../stores/aetitleStore";
import { ToastView } from "../mixins/toastView";

const AETitleView = React.createClass({
    getInitialState() {
        return {
            aetitleText: "",
            dirtyValue: false,  // aetitle value has unsaved changes
            status: "loading",
            showToast: false,
            toastType: 'default',
            toastMessage: {}
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
        if (data.success) {
            this.setState({
                toastType: "default",
                toastMessage: {
                    title: "Saved"
                }
            });
        } else {
            this.setState({
                toastType: "error",
                toastMessage: {
                    title: "Error",
                    body: data.message
                }
            });
        }

        if (this.state.status === "done") {
            this.setState(
                { showToast: true },
                () => setTimeout(() => this.setState({ showToast: false }), 3000)
            );
        }

        if (!data.success) {
            this.setState({
                dirtyValue: true
            });
        } else {
            this.setState({
                aetitleText: data.message,
                status: "done"
            });
        }
    },

    render() {
        const { showToast, toastType, toastMessage } = this.state;

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
                        aetitleText={this.state.aetitleText}
                        onChangeAETitle={this.handleAETitleChange}
                        onSubmitAETitle={this.handleSubmitAETitle}
                        dirtyValue={this.state.dirtyValue}
                    />
                </div>

                <ToastView show={showToast} message={toastMessage} toastType={toastType} />
            </div>
        );
    },

    handleAETitleChange(aetitle) {
        this.setState({
            aetitleText: aetitle,
            dirtyValue: true
        });
    },

    handleSubmitAETitle() {
        this.setState({
            dirtyValue: false
        });

        AETitleActions.setAETitle(this.state.aetitleText);
    }
});

export { AETitleView };
