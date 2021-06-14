import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'tag-custom-type',
        data: { pageTitle: 'schattenbuchhaltungApp.tagCustomType.home.title' },
        loadChildren: () => import('./tag-custom-type/tag-custom-type.module').then(m => m.TagCustomTypeModule),
      },
      {
        path: 'tag-custom-value',
        data: { pageTitle: 'schattenbuchhaltungApp.tagCustomValue.home.title' },
        loadChildren: () => import('./tag-custom-value/tag-custom-value.module').then(m => m.TagCustomValueModule),
      },
      {
        path: 'tag',
        data: { pageTitle: 'schattenbuchhaltungApp.tag.home.title' },
        loadChildren: () => import('./tag/tag.module').then(m => m.TagModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
