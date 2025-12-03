export const environment = {
  production: true,
  restApiBaseUrl: "/api",
  graphqlApiUrl: "/graphql",
  graphqlWsUrl: "/graphql",
  resApiUpdateUrl: "/api",
  keycloak: {
    serverUrl: "http://192.168.68.90:8080/auth",
    realm: "QLSX",
    clientId: "tem_in",
    adminApiUsers: "/auth/admin/realms/QLSX/users",
  },
  baseInTemApiUrl: "http://192.168.10.99:9000/api",
};
