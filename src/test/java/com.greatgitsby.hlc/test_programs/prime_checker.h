
{
This is a simple method of checking all numbers up to the
square root of a number to determine if it is a prime.
}

begin
    variable number;
    variable test;
    variable mult;
    variable remainder;
    variable flag;

    number := 7919;
    test := 2;
    flag := 0;
    mult := 4;

    while mult <= number do
    begin
        remainder := number;

        while remainder > 0 do
            remainder := remainder - test;

        if remainder = 0 then
            flag := 1;

        test := test + 1;
        mult := test * test
    end;
    print number;
    if flag = 0 then
        print " is prime"
    else
        print " is not prime";

    print "\n"
end
