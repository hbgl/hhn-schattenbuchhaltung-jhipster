import { NgModule } from '@angular/core';
import { SafeHtmlPipe } from './safe-html.pipe';
import { UniqueIdPipe } from './unique-id.pipe';

@NgModule({
  imports: [],
  declarations: [SafeHtmlPipe, UniqueIdPipe],
  exports: [SafeHtmlPipe, UniqueIdPipe],
})
export class PipesModule {}
