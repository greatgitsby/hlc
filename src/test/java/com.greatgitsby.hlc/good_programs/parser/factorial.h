{
    Fibonacci program
}

begin
    variable a;
    variable b;
    variable t;
    variable n;

    a := 1;
    b := 0;
    t := 0;

    { `n' is the nth Fibonnaci number }
    n := 25;

    print "fibonacci of ";
    print n;

    while n > 0 do
    begin
        t := a;
        a := a + b;
        b := t;
        n := n - 1
    end;

    print ": ";
    print b;
    print "\n"

end
