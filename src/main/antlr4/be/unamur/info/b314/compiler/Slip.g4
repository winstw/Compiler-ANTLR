grammar Slip;

import SlipWords;


program : (prog | map) EOF;

map : 'map' ':' NAT NAT line+;

line : ('@' | 'X' | 'G' | 'P' | 'A' | 'B' | 'T' | 'S' | '_' | 'Q')+;

prog : impDecl
            (enumDecl | varDecl | fctDecl | constDecl)* mainDecl;

mainDecl : 'main' 'as' 'function' '(' ')' ':' 'void' 'do' ((varDecl | instruction)* dig ';' (varDecl | instruction)*) 'end';

instBlock : (varDecl | enumDecl | constDecl)* instruction+;

argList : ID (',' ID)* 'as' type (',' ID (',' ID)* 'as' type)*;

fctDecl : ID 'as' 'function' '(' (argList)? ')' ':' (scalar | 'void') 'do' (instBlock)+ 'end';

impDecl : '#' 'import' FILENAME;

type : scalar
     | array
     | structure
     ;


scalar : 'boolean'
       | 'integer'
       | 'char'
       ;
number : ('-')? NAT;

array : scalar'[' number (',' number)? ']';

structure: 'record' (varDecl)+ 'end';

varDecl  : ID (',' ID)* 'as' type ('=' initVar)? ';' ;

initVar  : 'true'
         | 'false'
         | number
         | STRING
         | CHAR
         | exprEnt
         | exprBool
         | initArrays
         ;

initArrays: '(' (initVar (',' initVar)*)?')' ;

constDecl : 'const' ID 'as' type '=' initVar ';';

enumDecl : 'enum' ID '=' '(' ID (',' ID)* ')' ';' ;

exprD : STRING                                                  # string
      | CHAR                                                    # char
      | '(' exprD ')'                                           # parens
      | exprG                                                   # exprGExpr
      | ID'(' (exprD (','exprD)*)? ')'                          # funcExpr

      // exprEnt copied here to avoid indirect recursion
      | number                                                     # intExpr
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

exprEnt : number
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

actionType : 'left' '(' (exprD)? ')'
           | 'right' '(' (exprD)? ')'
           | 'up' '(' (exprD)? ')'
           | 'down' '(' (exprD)? ')'
           | 'jump' '(' (exprD)? ')'
           | 'fight' '(' ')'
           | dig
           ;

dig : 'dig' '(' ')';