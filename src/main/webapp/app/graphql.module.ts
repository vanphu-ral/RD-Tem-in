import { NgModule } from '@angular/core';
import { APOLLO_OPTIONS, Apollo } from 'apollo-angular';
import { HttpLink } from 'apollo-angular/http';
import { InMemoryCache } from '@apollo/client/core';
import { environment } from 'app/environments/environment';

import { ApolloClientOptions } from '@apollo/client/core';

export function createApollo(httpLink: HttpLink): ApolloClientOptions<any> {
  return {
    link: httpLink.create({ uri: environment.graphqlApiUrl }),
    cache: new InMemoryCache(),
  };
}

@NgModule({
  providers: [
    Apollo,
    {
      provide: APOLLO_OPTIONS,
      useFactory: createApollo,
      deps: [HttpLink],
    },
  ],
})
export class GraphQLModule {}
