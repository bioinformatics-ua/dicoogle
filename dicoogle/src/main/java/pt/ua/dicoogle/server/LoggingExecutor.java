/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.server;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A decorator for an {@linkplain java.util.concurrent.Executor}
 * which logs errors via slf4j.
 */
public class LoggingExecutor implements Executor {

    private final Executor inner;
    private final Logger logger;

    public LoggingExecutor(Executor executor, Logger logger) {
        java.util.Objects.requireNonNull(executor);
        java.util.Objects.requireNonNull(logger);
        this.inner = executor;
        this.logger = logger;
    }

    public LoggingExecutor(Executor executor) {
        this(executor, LoggerFactory.getLogger(LoggingExecutor.class));
    }

    @Override
    public String toString() {
        return "LoggingExecutor(" + super.toString() + ")";
    }

    @Override
    public void execute(Runnable command) {
        this.inner.execute(() -> {
            try {
                command.run();
            } catch (Exception ex) {
                logger.error("Command execution failed", ex);
            }
        });
    }
}
