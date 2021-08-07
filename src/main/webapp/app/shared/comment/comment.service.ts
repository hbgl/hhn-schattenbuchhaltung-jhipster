import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { CommentIn, Comment } from './comment.model';
import { map } from 'rxjs/operators';
import { plainToClass } from 'class-transformer';

@Injectable({ providedIn: 'root' })
export class CommentService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/comments');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(comment: CommentIn): Observable<Comment> {
    return this.http.post<Comment>(this.resourceUrl, comment).pipe(map(res => plainToClass(Comment, res)));
  }

  update(id: number, comment: CommentIn): Observable<Comment> {
    return this.http.put<Comment>(`${this.resourceUrl}/${id}`, comment).pipe(map(res => plainToClass(Comment, res)));
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.resourceUrl}/${id}`);
  }
}
