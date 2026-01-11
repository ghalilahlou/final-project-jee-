export const environment = {
    production: true,
    apiUrl: 'https://api.your-domain.com', // Production Gateway URL
    keycloak: {
        url: 'https://auth.your-domain.com',
        realm: 'ecommerce-realm',
        clientId: 'frontend-client'
    }
};
