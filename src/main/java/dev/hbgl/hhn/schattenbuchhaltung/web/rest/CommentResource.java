package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Comment;
import dev.hbgl.hhn.schattenbuchhaltung.domain.User;
import dev.hbgl.hhn.schattenbuchhaltung.repository.CommentRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.LedgerEntryRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.UserRepository;
import dev.hbgl.hhn.schattenbuchhaltung.security.AuthoritiesConstants;
import dev.hbgl.hhn.schattenbuchhaltung.security.SecurityUtils;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger.CommentCreateIn;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger.CommentOut;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger.CommentUpdateIn;
import dev.hbgl.hhn.schattenbuchhaltung.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link dev.hbgl.hhn.schattenbuchhaltung.domain.Comment}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CommentResource {

    private final Logger log = LoggerFactory.getLogger(CommentResource.class);

    private static final String ENTITY_NAME = "comment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EntityManager entityManager;

    private final CommentRepository commentRepository;

    private final LedgerEntryRepository ledgerEntryRepository;

    private final UserRepository userRepository;

    public CommentResource(
        EntityManager entityManager,
        CommentRepository commentRepository,
        LedgerEntryRepository ledgerEntryRepository,
        UserRepository userRepository
    ) {
        this.entityManager = entityManager;
        this.commentRepository = commentRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /comments} : Create a new comment.
     *
     * @param comment the comment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new comment, or with status {@code 400 (Bad Request)} if the comment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/comments")
    public ResponseEntity<CommentOut> createComment(@Valid @RequestBody CommentCreateIn input) throws URISyntaxException {
        log.debug("REST request to save Comment : {}", input);

        var maybeLedgerEntry = ledgerEntryRepository.findByNo(input.ledgerEntryNo);
        if (maybeLedgerEntry.isEmpty()) {
            throw new BadRequestAlertException("Related ledger entry not found", LedgerResource.LEDGER_ENTRY_ENTITY_NAME, "nonotfound");
        }
        var ledgerEntry = maybeLedgerEntry.get();

        Comment parent = null;
        if (input.parentId != null) {
            parent = entityManager.getReference(Comment.class, input.parentId);
        }

        var user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        var comment = new Comment();
        comment.setUuid(UUID.randomUUID());
        comment.setAuthor(user);
        comment.setLedgerEntry(ledgerEntry);
        comment.setParent(parent);
        comment.setCreatedAt(Instant.now());
        comment.setContentHtml(input.contentHtml);
        comment.setLedgerEntryNo(ledgerEntry.getNo());
        comment = commentRepository.save(comment);
        var output = CommentOut.fromEntity(comment);
        return ResponseEntity
            .created(new URI("/api/comments/" + comment.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, comment.getId().toString()))
            .body(output);
    }

    /**
     * {@code PUT  /comments/:id} : Updates an existing comment.
     *
     * @param id the id of the comment to save.
     * @param comment the comment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated comment,
     * or with status {@code 400 (Bad Request)} if the comment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the comment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentOut> updateComment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CommentUpdateIn input
    ) throws URISyntaxException {
        log.debug("REST request to update Comment : {}, {}", id, input);

        var maybeComment = commentRepository.findByIdWithAuthor(id);
        if (maybeComment.isEmpty()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        var comment = maybeComment.get();

        var user = userRepository.findOneWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        var isAuthorized = comment.isOwnedBy(user) || user.hasAuthority(AuthoritiesConstants.ADMIN);
        if (!isAuthorized) {
            throw new AccessDeniedException("Unauthorized");
        }

        comment.setContentHtml(input.contentHtml);
        comment = commentRepository.save(comment);

        var output = CommentOut.fromEntity(comment);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(output);
    }

    /**
     * {@code DELETE  /comments/:id} : delete the "id" comment.
     *
     * @param id the id of the comment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.debug("REST request to delete Comment : {}", id);

        var maybeComment = commentRepository.findByIdWithAuthor(id);
        if (maybeComment.isEmpty()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        var comment = maybeComment.get();

        var user = userRepository.findOneWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        var isAuthorized = comment.isOwnedBy(user) || user.hasAuthority(AuthoritiesConstants.ADMIN);
        if (!isAuthorized) {
            throw new AccessDeniedException("Unauthorized");
        }

        commentRepository.delete(comment);

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
