export const environment = {
  production: false,
  restApiBaseUrl: "http://localhost:8085",
  graphqlApiUrl: "http://localhost:8085/graphql",
  graphqlWsUrl: "ws://localhost:9040/graphql",
  resApiUpdateUrl: "http://localhost:8085",
  keycloak: {
    serverUrl: "http://192.168.68.90:8080/auth",
    realm: "QLSX",
    clientId: "tem_in_test",
    adminApiUsers: "/auth/admin/realms/QLSX/users",
  },
  baseInTemApiUrl: "http://192.168.10.99:8085/api",
};
