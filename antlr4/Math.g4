grammar Math;

expr: expr '*' term   # factor
    | expr '/' term   # factor
    | expr '+' term   # addExpr
    | expr '-' term   # addExpr
    | term            # terminal
    ;

term: NUM             # num
    | VAR             # var
    | '(' expr ')'    # parExpr
    ;

NUM: [0-9]+;
VAR: [A-Za-z][A-Za-z0-9]*;
WHITESPACE: [ \t\n]+ -> skip;