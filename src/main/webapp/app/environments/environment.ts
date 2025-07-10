export const environment = {
  restApiBaseUrl: "http://localhost:9000",
  graphqlApiUrl: "http://172.168.6.3:1433/graphql",
  graphqlWsUrl: "ws://172.168.6.3:1433/graphql",
  resApiUpdateUrl: "http://localhost:9000",
  keycloak: {
    serverUrl: "http://192.168.68.90:8080/auth",
    realm: "QLSX",
    clientId: "tem_in_test",
    adminApiUsers: "/auth/admin/realms/QLSX/users",
  },
};
