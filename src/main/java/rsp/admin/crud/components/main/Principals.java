package rsp.admin.crud.components.main;

import rsp.admin.auth.Principal;
import rsp.util.data.Tuple2;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class Principals {
    private final Map<String, Principal> principals = new ConcurrentHashMap<>();

    public Optional<Tuple2<String, Principal>> principal(Optional<String> deviceId) {
        return deviceId.flatMap(id -> Optional.ofNullable(principals.get(id)).map(p -> new Tuple2<>(id, p)));
    }

    public void login(String deviceId, Principal principal) {
        principals.put(deviceId, principal);
    }

    public Principal logout(String deviceId) {
        return principals.remove(deviceId);
    }

}
