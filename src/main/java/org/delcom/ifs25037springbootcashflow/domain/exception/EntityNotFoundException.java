package org.delcom.ifs25037springbootcashflow.domain.exception;

/**
 * Exception yang dilempar ketika entitas dengan ID tertentu tidak ditemukan.
 */
public class EntityNotFoundException extends DomainException {
    private final int id;

    public EntityNotFoundException(int id) {
        super("Data dengan ID " + id + " tidak ditemukan.");
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
