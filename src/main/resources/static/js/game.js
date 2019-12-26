$(document).ready(function () {
    var gameId;
    var errorMessage;
    $.ajax({
        url: 'http://localhost:8080/game',
        type: "POST",
        dataType: "json",
        success: function (gameData) {
            console.log(gameData);
            gameId = gameData.id;
            distributeStones(gameData);
        }
    });

    function distributeStones(gameData) {
        var board = gameData.board;
        $('#pit-1').text(board[1]);
        $('#pit-2').text(board[2]);
        $('#pit-3').text(board[3]);
        $('#pit-4').text(board[4]);
        $('#pit-5').text(board[5]);
        $('#pit-6').text(board[6]);
        $('#kalaha1').text(board[7]);
        $('#pit-8').text(board[8]);
        $('#pit-9').text(board[9]);
        $('#pit-10').text(board[10]);
        $('#pit-11').text(board[11]);
        $('#pit-12').text(board[12]);
        $('#pit-13').text(board[13]);
        $('#kalaha2').text(board[14]);
    }

    $(".pit").click(function () {
        var pitId = $(this).attr('id').split('-')[1];
        $.ajax({
            url: 'http://localhost:8080/game/' + gameId + "/move/" + pitId,
            type: "PUT",
            dataType: "json",
            success: function (gameData) {
                 console.log(gameData);
                 $('#message').text(gameData.message);
                 distributeStones(gameData);
            },
            error: function (e) {
                errorMessage = e.responseText;
                $('#message').text(errorMessage);
                console.log(errorMessage);
            }
        });
    });
});