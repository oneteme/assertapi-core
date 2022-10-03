package org.usf.assertapi.core;

import lombok.Getter;

@Getter
public enum ServerAuthMethod {
    BASIC,
    TOKEN,
    NOVA_BASIC,
    NOVA_TOKEN
}
