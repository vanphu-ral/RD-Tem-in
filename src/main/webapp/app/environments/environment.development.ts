export const environment = {
  production: false,
  restApiBaseUrl: "http://192.168.68.77:8085",
  graphqlApiUrl: "http://192.168.68.77:8085/graphql",
  graphqlWsUrl: "ws://192.168.68.77:8085/graphql",
  resApiUpdateUrl: "http://192.168.68.77:8085",
  keycloak: {
    serverUrl: "http://192.168.68.90:8080/auth",
    realm: "QLSX",
    clientId: "tem_in",
    adminApiUsers: "/auth/admin/realms/QLSX/users",
  },
  baseInTemApiUrl: "http://192.168.68.77:8085/api",
};
