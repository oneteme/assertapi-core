package org.usf.assertapi.core;

import lombok.Getter;

@Deprecated(forRemoval = true)
@Getter
public enum ServerAuthMethod {
    NO_AUTH,
    BASIC,
    TOKEN,
    NOVA_BASIC,
    NOVA_TOKEN
}
