grammar Slip;

import SlipWords;

program : impDecl
            ((varDecl)* | (fctDecl)* | (constDecl)* | (enumDecl)*)
            'main' 'as' 'function' '(' ')' ':' 'void'
            'do'
              ('dig()' | (instruction)*)
            'end'
            ;

instBlock : ((varDecl)* | (enumDecl)* | (constDecl)* | (structure)*) instruction+;

argList : ID (',' ID)* 'as' type (ID (',' ID)* 'as' type)*;

fctDecl : ID 'as' 'function' '(' (argList)? ')' ':' (scalar | 'void') 'do' (instBlock)+ 'end';

impDecl : '#import' FILENAME;

type : scalar
     | array
     | structure
     ;


scalar : 'boolean'
       | 'integer'
       | 'char'
       ;


array : scalar'[' INT (',' INT)? ']';

structure: ID 'as' 'record' (varDecl)+ 'end';

varDecl  : ID (',' ID)* 'as' type ('=' initVar)? ';' ;

initVar  : 'true'
         | 'false'
         | INT
         | STRING
         | CHAR
         | exprEnt
         | exprBool
         | initArrays
         ;

initArrays: '(' (initVar (',' initVar)*)?')' ;

constDecl : 'const' ID 'as' type '=' initVar ';';

enumDecl : 'enum' ID '=' '(' ID (',' ID)* ')' ';' ;

exprD : STRING      #STRINGExpr
      | CHAR       #char
      | '(' exprD ')'                                           # parens
      | exprG                                                   # exprGExpr
      | ID'(' (exprD (','exprD)*)? ')'                          # funcExpr

      // exprEnt copied here to avoid indirect recursion
      | INT                                                     # intExpr
      | '-' exprD                                               # unaryMinusExpr
      | exprD ('*' | '/' | '%') exprD                           # timesDivideExpr
      | exprD ('+' | '-') exprD                                 # plusMinusExpr

      // exprBool copied here to avoid indirect recursion
      | 'true'                                                  # trueExpr
      | 'false'                                                 # falseExpr
      | exprD op=('=' | '<' | '>' | '<=' | '>=' | '<>') exprD   # comparExpr
      | exprD op=('and' | 'or') exprD                           # andOrExpr
      | 'not' exprD                                             # notExpr
      ;

exprEnt : INT
        | '-' exprD
        | exprD ('*' | '/' | '%') exprD
        | exprD ('+' | '-') exprD
        ;

exprBool : 'true'
         | 'false'
         | exprD op=('='|'<' | '>' | '<=' | '>=' | '<>') exprD
         | exprD op=('and' | 'or') exprD
         | 'not' exprD
         ;

exprG : ID
      | ID'['exprD (','exprD)? ']'
      | exprG'.'ID
      ;


instruction: 'if' '(' exprD ')' 'then' instruction+ 'end'
           | 'if' '(' exprD ')' 'then' instruction+ 'else' instruction+ 'end'
           | 'while' '(' exprD ')' 'do' instruction+ 'end'
           | 'repeat' instruction+ 'until' '(' exprD ')' 'end'
           | 'for' ID ':=' exprD 'to' exprD 'do' instruction+ 'end'
           | exprG ':=' exprD ';'
           | exprD ';'
           | actionType ';'
           ;

actionType : 'left(' (exprD)? ')'
           | 'right(' (exprD)? ')'
           | 'up(' (exprD)? ')'
           | 'down(' (exprD)? ')'
           | 'jump(' (exprD)? ')'
           | 'fight()'
           | 'dig()'
           ;

