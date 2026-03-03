// src/main/java/br/org/postalis/training/rh/shared/application/CommandHandler.java
package br.org.postalis.training.rh.shared.application;

/**
 * Handler para um tipo específico de Command.
 */
public interface CommandHandler<C extends Command> {
    void handle(C command);
}