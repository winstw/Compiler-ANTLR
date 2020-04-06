grammar Slip;

import SlipWords;


program : (prog | map) EOF;

map : MAP COLON NAT NAT line+;

line : ('@' | 'X' | 'G' | 'P' | 'A' | 'B' | 'T' | 'S' | '_' | 'Q')+;

prog : impDecl (declaration | funcDecl | constDecl | enumDecl)* mainDecl;

mainDecl : MAIN AS FUNCTION LPAR RPAR COLON VOIDTYPE DO ((declaration | instruction | enumDecl)* dig SEMICOLON (declaration | instruction | enumDecl)*) END;

enumDecl : ENUM ID EQUAL LPAR ID (COMMA ID)* RPAR SEMICOLON;

instBlock : (declaration | constDecl | enumDecl)* instruction+;

argList : varDef(COMMA varDef)*;
varDef : ID (COMMA ID)* AS scalar;

funcDecl : ID AS FUNCTION LPAR (argList)? RPAR COLON funcType DO (instBlock)+ END;

funcType : scalar | VOIDTYPE;

impDecl : HASH IMPORT FILENAME;

scalar : BOOLEANTYPE
       | INTEGERTYPE
       | CHARTYPE
       ;

number : (MINUS)? NAT;

declaration: (varDecl | arrayDecl | structDecl) SEMICOLON;
varDecl : ID (COMMA ID)* AS scalar (EQUAL exprD)?;
arrayDecl : ID (COMMA ID)* AS scalar LBRACKET number (COMMA number)* RBRACKET (EQUAL initArrays)?;
structDecl : ID (COMMA ID)* AS STRUCT (declaration)+ END;

initVar : exprD
        | initArrays
        ;

initArrays : LPAR (initVar (COMMA initVar)*)? RPAR;

constDecl : CONST (constVar | constArray | constStruct) SEMICOLON;
constVar : ID AS scalar EQUAL exprD;
constArray : ID AS scalar LBRACKET number (COMMA number)* RBRACKET EQUAL initArrays ;
constStruct : ID AS STRUCT (declaration)+ END;

exprD : STRING                                  # string
      | CHAR                                    # char
      | LPAR exprD RPAR                         # parens
      | exprG                                   # exprGExpr
      | ID LPAR (exprD (COMMA exprD)*)? RPAR    # funcExpr

      // exprEnt copied here to avoid indirect recursion
      | number                                  # intExpr
      | MINUS exprD                             # unaryMinusExpr
      | exprD (TIMES | DIVIDE | MODULO) exprD   # timesDivideExpr
      | exprD (PLUS | MINUS) exprD              # plusMinusExpr

      // exprBool copied here to avoid indirect recursion
      | TRUE                                    # trueExpr
      | FALSE                                   # falseExpr
      | exprD op=(EQUAL | DIFF) exprD           # comparExpr
      | exprD op=(LT | GT | LTOE | GTOE) exprD  # comparIntExpr
      | exprD op=(AND | OR) exprD               # andOrExpr
      | NOT exprD                               # notExpr
      ;


exprG : ID                                      # leftExprID
      | ID LBRACKET exprD (COMMA exprD)? RBRACKET       # leftExprArray
      | exprG DOT exprG                            # leftExprRecord
      ;

instruction : IF LPAR exprD RPAR THEN instruction+ END                      # ifThenInstr
            | IF LPAR exprD RPAR THEN instruction+ ELSE instruction+ END    # ifThenElseInstr
            | WHILE LPAR exprD RPAR DO instruction+ END                     # whileInstr
            | REPEAT instruction+ UNTIL LPAR exprD RPAR END                 # untilInstr
            | FOR ID AFFECT exprD TO exprD DO instruction+ END              # forInstr
            | exprG AFFECT exprD SEMICOLON                                  # assignInstr
            | actionType SEMICOLON                                          # actionInstr
            ;

actionType : LEFT LPAR exprD? RPAR
           | RIGHT LPAR exprD? RPAR
           | UP LPAR exprD? RPAR
           | DOWN LPAR exprD? RPAR
           | JUMP LPAR exprD? RPAR
           | FIGHT LPAR RPAR
           | dig
           ;

dig : DIG LPAR RPAR;