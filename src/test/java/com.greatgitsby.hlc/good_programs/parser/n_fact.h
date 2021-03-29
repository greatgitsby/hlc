{ This is an iterative approach to n! }

begin
    variable n;
    variable result;
    variable count;

    n := 10;
    result := 1;
    count := 1;


    while n <> count do
    begin
        count := count + 1;
        result := result * count
    end;

    print n;
    print "! = ";
    print result;
    print "\n"

end