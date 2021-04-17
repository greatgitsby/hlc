{
This is the "school" method for checking
whether a number is prime
}

begin
	{input variable}
    variable n;
    {input variable minus 1}
    variable ns;
    {divide result 1: n / i}
    variable m1;
    {divide result 2: ns / i}
    variable m2;
    {result: 1 if prime, 0  if not prime}
    variable r;
    {index for while loop}
    variable i;

    n := 31;
    ns := n - 1;
    r := 1;
    i := 2;

    if n <= 1 then
    begin
        r := 0
    end
    else
    begin
        while i < n do
        begin
            {makeshift modulo zero}
            m1 := n / i;
            m2 := ns / i;
            if m1 <> m2 then
            begin
                r := 0;
                i := n
            end;
            i := i + 1
        end
    end;

    {output}
    print "The number ";
    print n;
    print " is ";
    if r = 0 then
    begin
        print "not "
    end;
    print "prime."

end
