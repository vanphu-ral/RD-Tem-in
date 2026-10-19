function setupProxy({ tls }) {
  const conf = [
    {
      context: [
        "/api",
        "/services",
        "/management",
        "/v3/api-docs",
        "/h2-console",
        "/oauth2",
        "/login",
        "/auth",
        "/health",
      ],
      target: `http${tls ? "s" : ""}://localhost:8085`,
      secure: false,
      changeOrigin: tls,
    },
    // {
    //   // SAP chuyển kho (OWTQ / WTQ1) — gọi qua dev server để không bị CORS
    //   context: ["/tem-in-info"],
    //   target: "http://192.168.68.86:9090",
    //   secure: false,
    //   changeOrigin: true,
    // },
  ];
  return conf;
}

module.exports = setupProxy;
