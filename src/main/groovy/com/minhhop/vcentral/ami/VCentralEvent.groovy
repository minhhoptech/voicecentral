package com.minhhop.vcentral.ami

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.ToString
import org.asteriskjava.manager.event.UserEvent

/**
 * Send custom event to Asterisk
 * @author Giang Le
 */
@ToString(includeNames = true)
class VCentralEvent extends UserEvent {
    enum Type {
        AGENT_LOGIN, AGENT_LOGOUT, AGENT_CREATED, AGENT_DELETED, DIAL, SEGMENT_STARTED, SEGMENT_END
    }
    String details
    Long id
    String params

    VCentralEvent(Type source, Long id, String details = '') {
        super(source);
        this.id = id;
        this.details = details;
    }

    VCentralEvent(Type source, Long id, Map params, String details = '') {
        super(source);
        this.id = id;
        this.params = new JsonBuilder(params).toString();
        this.details = details;
    }

    Map getParameters() {
        return (Map) new JsonSlurper().parseText(this.params);
    }
}
