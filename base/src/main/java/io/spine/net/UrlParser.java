/*
 * Copyright 2019, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.net;

import com.google.common.base.Splitter;
import io.spine.net.Uri.Protocol;
import io.spine.net.Uri.QueryParameter;
import io.spine.net.Uri.Schema;

/**
 * Parses given URL to {@link Uri} instance.
 */
@SuppressWarnings("CheckReturnValue") // of calls to methods of fields that are builders
final class UrlParser {

    private static final char SEMICOLON = ':';
    static final String PROTOCOL_ENDING = "://";
    static final char CREDENTIALS_ENDING = '@';
    static final char CREDENTIALS_SEPARATOR = SEMICOLON;
    static final char HOST_ENDING = '/';
    static final char HOST_PORT_SEPARATOR = SEMICOLON;

    static final char FRAGMENT_START = '#';
    static final char QUERIES_START = '?';
    static final char QUERY_SEPARATOR = '&';
    private static final Splitter querySplitter = Splitter.on(QUERY_SEPARATOR);

    private final String originalUrl;

    private Uri.Builder record;
    private String unProcessedInput;

    /**
     * Creates an new instance of {@code UrlParser} with given String URL to parse.
     *
     * @param url String URL to parse
     */
    UrlParser(String url) {
        this.originalUrl = url;
    }

    /**
     * Performs the parsing.
     */
    Uri parse() {
        init();

        parseProtocol();
        parseCredentials();
        parseFragment();
        parseQueries();
        parseHost();
        parsePath();


        return record.build();
    }

    /** Initializes the parser. */
    private void init() {
        record = Uri.newBuilder();
        unProcessedInput = originalUrl;
    }

    /**
     * Parses protocol from remembered URL String and saves it to the state.
     *
     * <ul>
     *     <li>If no suitable protocol found, saves UNDEFINED value.
     *     <li>If some value is found but the schema is unknown, saves raw value.
     * </ul>
     */
    private void parseProtocol() {
        Protocol.Builder protocolBuilder = Protocol.newBuilder();
        int protocolEndingIndex = unProcessedInput.indexOf(PROTOCOL_ENDING);
        if (protocolEndingIndex == -1) {
            protocolBuilder.setSchema(Schema.UNDEFINED);
            record.setProtocol(protocolBuilder);
            return;
        }
        String protocol = unProcessedInput.substring(0, protocolEndingIndex);
        unProcessedInput = unProcessedInput.substring(protocolEndingIndex +
                                                      PROTOCOL_ENDING.length());

        Schema schema = Schemas.parse(protocol);

        if (schema == Schema.UNDEFINED) {
            protocolBuilder.setName(protocol);
        } else {
            protocolBuilder.setSchema(schema);
        }

        record.setProtocol(protocolBuilder.build());
    }

    /** Parses credentials from remembered URL String and saves them to the state. */
    private void parseCredentials() {
        int credentialsEndingIndex = unProcessedInput.indexOf(CREDENTIALS_ENDING);
        if (credentialsEndingIndex == -1) {
            return;
        }

        String credential = unProcessedInput.substring(0, credentialsEndingIndex);
        unProcessedInput = unProcessedInput.substring(credentialsEndingIndex + 1);

        Uri.Authorization.Builder auth = Uri.Authorization.newBuilder();

        int credentialsSeparatorIndex = credential.indexOf(CREDENTIALS_SEPARATOR);
        if (credentialsSeparatorIndex != -1) {
            String userName = credential.substring(0, credentialsSeparatorIndex);
            String password = credential.substring(credentialsSeparatorIndex + 1);
            auth.setPassword(password);
            auth.setUserName(userName);
        } else {
            auth.setUserName(credential);
        }

        record.setAuth(auth.build());
    }

    /** Parses host and port and saves them to the state. */
    private void parseHost() {
        int hostEndingIndex = unProcessedInput.indexOf(HOST_ENDING);
        String host;

        if (hostEndingIndex == -1) {
            host = unProcessedInput;
            unProcessedInput = "";
        } else {
            host = unProcessedInput.substring(0, hostEndingIndex);
            unProcessedInput = unProcessedInput.substring(hostEndingIndex + 1);
        }

        int portIndex = host.indexOf(HOST_PORT_SEPARATOR);
        if (portIndex != -1) {
            String port = host.substring(portIndex + 1);
            record.setPort(port);
            String hostAddress = host.substring(0, portIndex);
            record.setHost(hostAddress);
        } else {
            record.setHost(host);
        }
    }

    /** Parses fragment and saves it to the state. */
    private void parseFragment() {
        int fragmentIndex = unProcessedInput.lastIndexOf(FRAGMENT_START);
        if (fragmentIndex == -1) {
            return;
        }

        String fragment = unProcessedInput.substring(fragmentIndex + 1);
        unProcessedInput = unProcessedInput.substring(0, fragmentIndex);

        record.setFragment(fragment);
    }

    /**
     * Parses query parameters and saves them to the state.
     *
     * @throws IllegalArgumentException in case of bad-formed parameter
     */
    private void parseQueries() {
        int queriesStartIndex = unProcessedInput.indexOf(QUERIES_START);
        if (queriesStartIndex == -1) {
            return;
        }

        String queriesString = unProcessedInput.substring(queriesStartIndex + 1);
        unProcessedInput = unProcessedInput.substring(0, queriesStartIndex);

        Iterable<String> queries = querySplitter.split(queriesString);
        for (String query : queries) {
            QueryParameter param = UrlQueryParameters.parse(query);
            record.addQuery(param);
        }
    }

    /** Parses the URL resource path from the remaining part of URL. */
    private void parsePath() {
        if (unProcessedInput.isEmpty()) {
            return;
        }
        record.setPath(unProcessedInput);
        unProcessedInput = "";
    }
}
