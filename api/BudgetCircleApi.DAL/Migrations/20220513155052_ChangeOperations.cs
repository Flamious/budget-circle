using System;
using Microsoft.EntityFrameworkCore.Migrations;

namespace BudgetCircleApi.DAL.Migrations
{
    public partial class ChangeOperations : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Date",
                table: "ScheduledOperation");

            migrationBuilder.DropColumn(
                name: "Period",
                table: "ScheduledOperation");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<DateTime>(
                name: "Date",
                table: "ScheduledOperation",
                type: "datetime2",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<int>(
                name: "Period",
                table: "ScheduledOperation",
                type: "int",
                nullable: false,
                defaultValue: 0);
        }
    }
}
