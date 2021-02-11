package net.minestom.server.command.builder.arguments;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.ArgumentCallback;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.NodeMaker;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An argument is meant to be parsed when added into a {@link Command}'s syntax with {@link Command#addSyntax(CommandExecutor, Argument[])}.
 * <p>
 * You can create your own with your own special conditions.
 * <p>
 * Arguments are parsed using {@link #parse(String)}.
 *
 * @param <T> the type of this parsed argument
 */
public abstract class Argument<T> {

    protected final static CommandManager COMMAND_MANAGER = MinecraftServer.getCommandManager();

    private final String id;
    private final boolean allowSpace;
    private final boolean useRemaining;
    private Argument<?> redirect;

    private ArgumentCallback callback;

    private T defaultValue;

    /**
     * Creates a new argument.
     *
     * @param id           the id of the argument, used to retrieve the parsed value
     * @param allowSpace   true if the argument can/should have spaces in it
     * @param useRemaining true if the argument will always take the rest of the command arguments
     */
    public Argument(@NotNull String id, boolean allowSpace, boolean useRemaining) {
        this.id = id;
        this.allowSpace = allowSpace;
        this.useRemaining = useRemaining;
    }

    /**
     * Creates a new argument with {@code useRemaining} sets to false.
     *
     * @param id         the id of the argument, used to retrieve the parsed value
     * @param allowSpace true if the argument can/should have spaces in it
     */
    public Argument(@NotNull String id, boolean allowSpace) {
        this(id, allowSpace, false);
    }

    /**
     * Creates a new argument with {@code useRemaining} and {@code allowSpace} sets to false.
     *
     * @param id the id of the argument, used to retrieve the parsed value
     */
    public Argument(@NotNull String id) {
        this(id, false, false);
    }

    /**
     * Parses the given input, and throw an {@link ArgumentSyntaxException}
     * if the input cannot be convert to {@code T}
     *
     * @param input the argument to parse
     * @return the parsed argument
     * @throws ArgumentSyntaxException if {@code value} is not valid
     */
    @NotNull
    public abstract T parse(@NotNull String input) throws ArgumentSyntaxException;

    /**
     * Turns the argument into a list of nodes for command dispatching. Make sure to set the Node's parser.
     *
     * @param nodeMaker  helper object used to create and modify nodes
     * @param executable true if this will be the last argument, false otherwise
     */
    public abstract void processNodes(@NotNull NodeMaker nodeMaker, boolean executable);

    /**
     * Builds an argument node.
     *
     * @param argument   the argument
     * @param executable true if this will be the last argument, false otherwise
     * @return the created {@link DeclareCommandsPacket.Node}
     */
    @NotNull
    protected static DeclareCommandsPacket.Node simpleArgumentNode(@NotNull Argument<?> argument,
                                                                   boolean executable, boolean redirect, boolean suggestion) {
        DeclareCommandsPacket.Node argumentNode = new DeclareCommandsPacket.Node();

        argumentNode.flags = COMMAND_MANAGER.getFlag(CommandManager.NodeType.ARGUMENT, executable, redirect, suggestion);
        argumentNode.name = argument.getId();

        return argumentNode;
    }

    /**
     * Gets the ID of the argument, showed in-game above the chat bar
     * and used to retrieve the data when the command is parsed in {@link net.minestom.server.command.builder.Arguments}.
     *
     * @return the argument id
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Gets if the argument can contain space.
     *
     * @return true if the argument allows space, false otherwise
     */
    public boolean allowSpace() {
        return allowSpace;
    }

    /**
     * Gets if the argument always use all the remaining characters.
     * <p>
     * ex: /help I am a test - will always give you "I am a test"
     * if the first and single argument does use the remaining.
     *
     * @return true if the argument use all the remaining characters, false otherwise
     */
    public boolean useRemaining() {
        return useRemaining;
    }

    /**
     * Gets the {@link ArgumentCallback} to check if the argument-specific conditions are validated or not.
     *
     * @return the argument callback, null if not any
     */
    @Nullable
    public ArgumentCallback getCallback() {
        return callback;
    }

    /**
     * Sets the {@link ArgumentCallback}.
     *
     * @param callback the argument callback, null to do not have one
     */
    public void setCallback(@Nullable ArgumentCallback callback) {
        this.callback = callback;
    }

    /**
     * Gets if this argument is 'optional'.
     * <p>
     * Optional means that this argument can be put at the end of a syntax
     * and obtains a default value ({@link #getDefaultValue()}).
     *
     * @return true if this argument is considered optional
     */
    public boolean isOptional() {
        return defaultValue != null;
    }

    /**
     * Gets the default value of this argument.
     *
     * @return the argument default value, null if the argument is not optional
     */
    @Nullable
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value of the argument.
     * <p>
     * A non-null value means that the argument can be put at the end of a syntax
     * to act as an optional one.
     *
     * @param defaultValue the default argument value, null to make the argument non-optional
     * @return 'this' for chaining
     */
    @NotNull
    public Argument<T> setDefaultValue(@Nullable T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Gets the argument this argument will redirect to.
     * <p>
     * A non-null value means that this will redirect to that argument.
     *
     * @return The argument this argument will redirect to
     */
    @Nullable
    public Argument<?> getRedirect() {
        return redirect;
    }

    /**
     * Checks if an argument has a redirect.
     * <p>
     * Mostly used to make sure there isnt an argument after.
     *
     * @return If this argument will redirect to an argument.
     */
    public boolean hasRedirect() {
        return redirect != null;
    }

    /**
     * Sets the redirect of this argument.
     *
     * @param argument The argument to set the redirect to
     */
    public void setRedirect(Argument<?> argument) {
        this.redirect = argument;
    }

    /**
     * Gets if the argument has any error callback.
     *
     * @return true if the argument has an error callback, false otherwise
     */
    public boolean hasErrorCallback() {
        return callback != null;
    }

}
