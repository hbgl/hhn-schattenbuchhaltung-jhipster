import { Injectable } from '@angular/core';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import Dexie from 'dexie';
import { CommentCollapseState } from './comment-collapse-state';

@Injectable({
  providedIn: 'root',
})
export class IndexedDbService {
  private dexie: Dexie;

  constructor(configService: ApplicationConfigService) {
    this.dexie = new Dexie(configService.indexedDbName);

    this.dexie.version(1).stores({
      commentCollapseStates: 'id',
    });
  }

  public get db(): Dexie {
    return this.dexie;
  }

  public get commentCollapseStates(): Dexie.Table<CommentCollapseState, number> {
    return this.dexie.table('commentCollapseStates');
  }
}
