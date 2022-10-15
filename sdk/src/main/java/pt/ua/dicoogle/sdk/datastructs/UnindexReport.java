/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk.
 *
 * Dicoogle/dicoogle-sdk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk.datastructs;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/** Describes a report for a bulk unindexing operation.
 */
public final class UnindexReport implements Serializable {

    /** The description of a file which
     * could not be unindexed due to an error.
     * 
     * When an error of this kind occurs,
     * it is not specified whether the file remains indexed or not.
     */
    public static final class FailedUnindex implements Serializable {
        /** The URI to the item which failed to unindex. */
        public final URI uri;

        /** The exception describing the error which led to the failure.
         * This field can be <code>null</code>
         * when no cause is specified.
         */
        public final Exception cause;

        /** Creates a failed unindex description
         * due to the file not being found in the database.
         * 
         * @param uri the URI of the file which could not be unindexed
         * @param cause the underlying exception, if any
         */
        public FailedUnindex(URI uri, Exception cause) {
            Objects.requireNonNull(uri);
            this.uri = uri;
            this.cause = cause;
        }
    }

    /** URIs of files which were not found. */
    private final Collection<URI> notFound;
    private final Collection<FailedUnindex> failures;

    /** Creates a full report for a bulk unindexing operation.
     * All parameters are nullable,
     * in which case is equivalent to passing an empty collection.
     * @param notFound the URIs of files which were not found
     * @param failures the error reports of files which could not be unindexed
     */
    public UnindexReport(Collection<URI> notFound, Collection<FailedUnindex> failures) {
        if (notFound == null) {
            notFound = Collections.emptyList();
        }
        if (failures == null) {
            failures = Collections.emptyList();
        }
        this.notFound = notFound;
        this.failures = failures;
    }

    /** Creates a report that all files were successfully unindexed. */
    public static UnindexReport ok() {
        return new UnindexReport(null, null);
    }

    /** Creates a report with the files which failed to unindex
     * due to some error.
     */
    public static UnindexReport withFailures(Collection<FailedUnindex> failures) {
        return new UnindexReport(null, failures);
    }

    /** Returns whether all files were successfully unindexed from the database
     * as requested.
     */
    public boolean isOk() {
        return notFound.isEmpty() && failures.isEmpty();
    }

    /** Returns whether all files are no longer unindexed,
     * meaning that no errors occurred when trying to unindex an indexed file.
     * 
     * This is different from {@link #isOk()} in that
     * it does not imply that all files to unindex were found in the database.
     * 
     * @return true if no unindex failures are reported other than files not found
     */
    public boolean allUnindexed() {
        return failures.isEmpty();
    }

    /** Obtains an immutable collection to
     * the files which failed to unindex due to an error.
     */
    public Collection<FailedUnindex> getUnindexFailures() {
        return Collections.unmodifiableCollection(this.failures);
    }

    /** Obtains an immutable collection to the files
     *  which were not found in the index.
     */
    public Collection<URI> getNotFound() {
        return Collections.unmodifiableCollection(this.notFound);
    }
}
