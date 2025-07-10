export const environment = {
  restApiBaseUrl: "http://localhost:9000",
  graphqlApiUrl: "http://localhost:8085",
  graphqlWsUrl: "http://localhost:8085",
  resApiUpdateUrl: "http://localhost:9000",
  keycloak: {
    serverUrl: "http://192.168.68.90:8080/auth",
    realm: "QLSX",
    clientId: "tem_in_test",
    adminApiUsers: "/auth/admin/realms/QLSX/users",
  },
};
