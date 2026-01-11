export const environment = {
    production: false,
    apiUrl: 'http://localhost:8081', // Gateway URL
    keycloak: {
        url: 'http://localhost:8080',
        realm: 'ecommerce-realm',
        clientId: 'frontend-client'
    }
};
