// Generated from Math.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MathParser}.
 */
public interface MathListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code addExpr}
	 * labeled alternative in {@link MathParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddExpr(MathParser.AddExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code addExpr}
	 * labeled alternative in {@link MathParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddExpr(MathParser.AddExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code terminal}
	 * labeled alternative in {@link MathParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterTerminal(MathParser.TerminalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code terminal}
	 * labeled alternative in {@link MathParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitTerminal(MathParser.TerminalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code factor}
	 * labeled alternative in {@link MathParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFactor(MathParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code factor}
	 * labeled alternative in {@link MathParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFactor(MathParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code num}
	 * labeled alternative in {@link MathParser#term}.
	 * @param ctx the parse tree
	 */
	void enterNum(MathParser.NumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code num}
	 * labeled alternative in {@link MathParser#term}.
	 * @param ctx the parse tree
	 */
	void exitNum(MathParser.NumContext ctx);
	/**
	 * Enter a parse tree produced by the {@code var}
	 * labeled alternative in {@link MathParser#term}.
	 * @param ctx the parse tree
	 */
	void enterVar(MathParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code var}
	 * labeled alternative in {@link MathParser#term}.
	 * @param ctx the parse tree
	 */
	void exitVar(MathParser.VarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parExpr}
	 * labeled alternative in {@link MathParser#term}.
	 * @param ctx the parse tree
	 */
	void enterParExpr(MathParser.ParExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parExpr}
	 * labeled alternative in {@link MathParser#term}.
	 * @param ctx the parse tree
	 */
	void exitParExpr(MathParser.ParExprContext ctx);
}