
{
This is an iterative approach to determine the fibonacci number
of a given number n
}

begin
    variable n;
    variable a;
    variable b;
    variable i;
    variable c;
    
    {
    Change the value of n to change the output of the program.  Whatever
    the value of n is, is the value we are finding the fib sequence of.
    }
    n := 10;
    a := 1;
    b := 1;
    i := 3;
    
    while i <= n do
    begin
        c := a + b;
        a := b;
        b := c;
        i := i + 1
    end;  
             
    print b;
    print "\n"
end 
