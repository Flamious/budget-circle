using Microsoft.EntityFrameworkCore.Migrations;

namespace BudgetCircleApi.DAL.Migrations
{
    public partial class AddPlannedBudget : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "PlannedBudget",
                columns: table => new
                {
                    Id = table.Column<int>(nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Month = table.Column<int>(nullable: false),
                    Year = table.Column<int>(nullable: false),
                    PlannedEarnings = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    PlannedExpenses = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    UserId = table.Column<string>(nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PlannedBudget", x => x.Id);
                    table.ForeignKey(
                        name: "FK_PlannedBudget_AspNetUsers_UserId",
                        column: x => x.UserId,
                        principalTable: "AspNetUsers",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateIndex(
                name: "IX_PlannedBudget_UserId",
                table: "PlannedBudget",
                column: "UserId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "PlannedBudget");
        }
    }
}
