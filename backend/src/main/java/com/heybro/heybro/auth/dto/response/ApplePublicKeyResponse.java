package com.heybro.heybro.auth.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class ApplePublicKeyResponse {

    private List<Key> keys;

    @Getter
    @NoArgsConstructor
    public static class Key {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }

    public Optional<Key> getMatchedKey(String kid, String alg) {
        return this.keys.stream()
                .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
                .findFirst();
    }
}
