package au.com.example.security.constant;

public class Constants {

    // === Security ===
    public static final String SECURITY_UNAUTH_ENTRY_POINT = "unauthorisedEntryPoint";
    public static final String SECURITY_STATELESS_AUTH_FILTER = "statelessAuthFilter";
    public static final String SECURITY_STATELESS_TOKEN_AUTH_FILTER = "statelessTokenAuthFilter";
    public static final String SECURITY_TOKEN_HANDLER = "tokenHandler";
    public static final String SECURITY_TOKEN_SECURITY_CHANNEL_INTERCEPTOR = "tokenSecurityChannelInterceptor";

    // === Services ===
    public static final String SERVICE_USER_DETAIL = "userDetail";
    public static final String SERVICE_TOKEN_AUTH = "tokenAuth";

    // === Tables ===
    public static final String TABLE_MEMBERSHIP = "membership";
    public static final String TABLE_USER = "user";
    public static final String TABLE_USER_MEMBERSHIP = "user_membership";

    // === Entities ===
    public static final String ENTITY_USER = "UserEntity";

    // === Identifiers ===
    public static final String HEADER_X_AUTH_TOKEN = "X-AUTH-TOKEN";
}
