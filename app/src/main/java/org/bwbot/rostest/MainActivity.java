package org.bwbot.rostest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.ros.android.RosActivity;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;

import java.net.URI;

import std_msgs.String;

public class MainActivity extends RosActivity {

    protected MainActivity() {
        super("ros_test", "ros_test", URI.create("http://192.168.0.23:11311"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(new NodeMain() {
            @Override
            public GraphName getDefaultNodeName() {
                return GraphName.of("ros_test");
            }

            @Override
            public void onStart(ConnectedNode connectedNode) {
                final Publisher<std_msgs.String> pub =  connectedNode.newPublisher("/test", String._TYPE);
                connectedNode.executeCancellableLoop(new CancellableLoop() {
                    @Override
                    protected void loop() throws InterruptedException {
                        std_msgs.String msg = pub.newMessage();
                        msg.setData("hello world");
                        pub.publish(msg);
                        Thread.sleep(1000);
                    }
                });
            }

            @Override
            public void onShutdown(Node node) {

            }

            @Override
            public void onShutdownComplete(Node node) {

            }

            @Override
            public void onError(Node node, Throwable throwable) {

            }
        }, nodeConfiguration);
    }
}
