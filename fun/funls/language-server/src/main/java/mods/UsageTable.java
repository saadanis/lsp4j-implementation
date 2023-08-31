package mods;

import java.util.HashMap;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * The UsageTable class represents a table that tracks the usage of identifiers in different scopes.
 * It maintains separate maps for global and local scope identifiers.
 */
public class UsageTable {
	
	private HashMap<String, ParserRuleContext> globals, locals;
	
    /**
     * Constructor for UsageTable.
     */
	public UsageTable() {
		
		globals = new HashMap<String,ParserRuleContext>();
		locals = null;
	}
	
	/**
     * Adds an identifier and its associated ParserRuleContext to the appropriate scope.
     *
     * @param id  The identifier to be added.
     * @param ctx The ParserRuleContext associated with the identifier.
     */
	public void put(String id, ParserRuleContext ctx) {
		
		if (!id.equals("main")) {
			
            // Determine the scope based on the presence of locals map.
			HashMap<String,ParserRuleContext> scope = (locals != null ? locals : globals);
			scope.put(id, ctx);
		}
	}
	
	/**
     * Marks an identifier as used and removes it from the appropriate scope.
     *
     * @param id The identifier to be marked as used.
     */
	public void use(String id) {
		
		if (locals != null && locals.get(id) != null)
			locals.remove(id);
		else
			globals.remove(id);
	}
	
	/**
     * Enters a new local scope by initializing the locals map.
     */
	public void enterScope() {
		
		locals = new HashMap<String, ParserRuleContext>();
	}
	
	/**
     * Exits the current scope and returns a map of unused identifiers in that scope.
     *
     * @return A map of unused identifiers and their associated ParserRuleContext.
     */
	public HashMap<String,ParserRuleContext> exitScope() {
		
		HashMap<String,ParserRuleContext> unused = new HashMap<String,ParserRuleContext>();
		
		if (locals != null) {
			locals.forEach((id, ctx) -> {
				unused.put(id, ctx);
			});
			locals = null;
		} else {
			globals.forEach((id, ctx) -> {
				unused.put(id, ctx);
			});
		}
		
		return unused;
	}
}
