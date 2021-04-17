{
cepperson16@georgefox.edu
Non-trivial Program
2019-9-27
}

{
This program calculates and prints the Nth Fibonacci number.
}

begin
    variable N;
    variable i;

    variable previous;
    variable current;
    variable next;

    previous := 1;
    current := 1;
    next := 1;

    N := 25;

    i := 1;
    while i < N do
    begin
        previous := current;
        current := next;

        next := current + previous;

        i := i + 1
    end;

    print current
end
