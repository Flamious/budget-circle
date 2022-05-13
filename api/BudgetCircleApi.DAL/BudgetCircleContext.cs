namespace BudgetCircleApi.DAL
{
    using BudgetCircleApi.DAL.Entities;
    using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
    using Microsoft.EntityFrameworkCore;

    public class BudgetCircleContext : IdentityDbContext
    {
        public BudgetCircleContext(DbContextOptions<BudgetCircleContext> options)
            : base(options)
        {
        }

        public virtual DbSet<Operation> Operation { get; set; }

        public virtual DbSet<BudgetType> BudgetType { get; set; }

        public virtual DbSet<ExpenseType> ExpenseType { get; set; }

        public virtual DbSet<EarningType> EarningType { get; set; }

        public virtual DbSet<User> User { get; set; }

        protected override void OnModelCreating(ModelBuilder builder)
        {
            base.OnModelCreating(builder);
            builder.Entity<User>(entity =>
            {
                entity.Property(e => e.Email).IsRequired();
                entity.Property(e => e.PasswordHash).IsRequired();
            });

            builder.Entity<Operation>(entity =>
            {
                entity.Property(e => e.Title).IsRequired();
                entity.Property(e => e.Sum).IsRequired();

                entity
                .HasOne(e => e.User)
                .WithMany(e => e.Operations)
                .HasForeignKey(e => e.UserId)
                .OnDelete(DeleteBehavior.Restrict);

                entity
                .HasOne(e => e.Type)
                .WithMany(e => e.Operations)
                .HasForeignKey(e => e.TypeId)
                .OnDelete(DeleteBehavior.Restrict);
            });
        }
    }
}
