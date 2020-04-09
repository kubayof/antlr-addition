package com.naofi.antlr;// Generated from Math.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MathParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MathVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code addExpr}
	 * labeled alternative in {@link MathParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpr(MathParser.AddExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code terminal}
	 * labeled alternative in {@link MathParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerminal(MathParser.TerminalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code factor}
	 * labeled alternative in {@link MathParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(MathParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code num}
	 * labeled alternative in {@link MathParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNum(MathParser.NumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code var}
	 * labeled alternative in {@link MathParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(MathParser.VarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parExpr}
	 * labeled alternative in {@link MathParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParExpr(MathParser.ParExprContext ctx);
}