package com.minhhop.vcentral.ami

import org.apache.log4j.Logger
import org.asteriskjava.AsteriskVersion
import org.asteriskjava.manager.ManagerConnection
import org.asteriskjava.manager.ManagerConnectionFactory
import org.asteriskjava.manager.action.UserEventAction

import static org.asteriskjava.manager.ManagerConnectionState.*;

/**
 * Server handler
 * @author Giang Le<giangle@minhhop.net>
 */
class VCentralManager implements Serializable {
    private static Logger log = Logger.getLogger(VCentralManager.class);
    // Asterisk AMI connect information
    private String host;
    private String port = 5038;
    private String username;
    private String password;

    private ManagerConnection connection;
    private static VCentralManager _instance;

    /**
     * Creates a new VCentralManager with the given connection data and
     * the default port 5038.
     *
     * @param hostname the hostname of the Asterisk server to connect to.
     * @param username the username to use for login as defined in Asterisk's <code>manager.conf</code>.
     * @param password the password to use for login as defined in Asterisk's <code>manager.conf</code>.
     * @since 0.1
     */
    public static VCentralManager getInstance(String host, String username, String password) {
        if (!_instance) {

        }
        return _instance;
    }

    /**
     * Creates a new VCentralManager with the given connection data and
     * the default port 5038.
     *
     * @param hostname the hostname of the Asterisk server to connect to.
     * @param username the username to use for login as defined in Asterisk's <code>manager.conf</code>.
     * @param password the password to use for login as defined in Asterisk's <code>manager.conf</code>.
     * @since 0.3
     */
    private VCentralManager(String host, String username, String password) throws IOException {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    void sendEvent(VCentralEvent event) {
        this.reconnect();
        this.connection.sendAction(new UserEventAction(event));
    }

    /**
     * Check connection state. Reconnecting if needed
     */
    void reconnect() {
        if (!this.connection) {
            this.connect();
        }
        // check connection status
        if (this.connection.state in  [INITIAL, DISCONNECTED]) {
            log.debug "Connect for the first time....."
            login();
        } else if (this.connection.state == RECONNECTING) {
            logoff(); login();
        } else if (this.connection.state == DISCONNECTING) {
            this.connection = null;
            this.connect();
            login();
        }
    }

    /**
     * Sends a LogoffAction to the Asterisk server and disconnects.
     *
     * @throws IllegalStateException if not in state CONNECTED or RECONNECTING.
     * @see org.asteriskjava.manager.action.LogoffAction
     */
    void logoff() {
        this.connection.logoff();
    }

    /**
     * Logs in to the Asterisk server with the username and password specified
     * when this connection was created.
     *
     * @throws IllegalStateException if connection is not in state INITIAL or
     *             DISCONNECTED.
     * @throws IOException if the network connection is disrupted.
     * @throws org.asteriskjava.manager.AuthenticationFailedException if the username and/or password are
     *             incorrect or the ChallengeResponse could not be built.
     * @throws org.asteriskjava.manager.TimeoutException if a timeout occurs while waiting for the
     *             protocol identifier. The connection is closed in this case.
     * @see org.asteriskjava.manager.action.LoginAction
     * @see org.asteriskjava.manager.action.ChallengeAction
     */
    void login() {
        this.connection.login();
    }

    /**
     *  Created connection to the Asterisk server.
     */
    void connect() {
        this.connection = new ManagerConnectionFactory(this.host, this.username, this.password).createManagerConnection();
    }
}
